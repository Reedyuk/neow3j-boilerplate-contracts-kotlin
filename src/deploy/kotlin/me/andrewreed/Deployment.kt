package me.andrewreed

import io.neow3j.compiler.Compiler
import io.neow3j.contract.ContractManagement
import io.neow3j.contract.GasToken
import io.neow3j.contract.SmartContract
import io.neow3j.crypto.ECKeyPair
import io.neow3j.protocol.Neow3j
import io.neow3j.protocol.http.HttpService
import io.neow3j.transaction.AccountSigner
import io.neow3j.types.ContractParameter
import io.neow3j.types.Hash160
import io.neow3j.types.NeoVMStateType
import io.neow3j.utils.Await
import io.neow3j.utils.Numeric
import io.neow3j.wallet.Account


object Deployment {

    const val NEO_NETWORK = "http://localhost:50012"
    const val ALICE_PK = "6c54536dbd876b92bfc96dd7b9fd6a4286d9a51ac5e26b5cf9becfa27e330918"

    @JvmStatic
    fun main(args: Array<String>) {
        val neow3j = Neow3j.build(HttpService(NEO_NETWORK))

        val ecKeyPair = ECKeyPair.create(Numeric.hexStringToByteArray(ALICE_PK))
        val deploymentAccount = Account.fromWIF(ecKeyPair.exportAsWIF())
        if (GasToken(neow3j).getBalanceOf(deploymentAccount).toInt() == 0) {
            throw RuntimeException(
                "Alice has no GAS. If you're running a neo express instance run `neoxp " +
                        "transfer 100 GAS genesis alice` in a terminal in the root directory of this project."
            )
        }
        val signer = AccountSigner.none(deploymentAccount)

        val substitutions: MutableMap<String, String> = HashMap()
        substitutions["\${placeholder}"] = "my andrew value"

        deployHelloWorldSmartContract(
            signer,
            deploymentAccount.scriptHash,
            substitutions,
            neow3j
        )
    }

    @Throws(Throwable::class)
    private fun deployHelloWorldSmartContract(
        signer: AccountSigner, owner: Hash160,
        substitutions: Map<String, String>, neow3j: Neow3j
    ): Hash160? {
        val res = Compiler().compile(HelloWorldSmartContract::class.java.canonicalName, substitutions)
        val builder = ContractManagement(neow3j)
            .deploy(res.nefFile, res.manifest, ContractParameter.hash160(owner))
            .signers(signer)
        val txHash = builder.sign().send().sendRawTransaction.hash
        println("Deployment Transaction Hash: $txHash")
        Await.waitUntilTransactionIsExecuted(txHash, neow3j)
        val log = neow3j.getApplicationLog(txHash).send().applicationLog
        if (log.executions[0].state == NeoVMStateType.FAULT) {
            throw Exception(
                "Failed to deploy contract. NeoVM error message: " + log.executions[0].exception
            )
        }
        val contractHash = SmartContract.calcContractHash(
            signer.scriptHash,
            res.nefFile.checkSumAsInteger, res.manifest.name
        )
        println("Contract Hash: $contractHash")
        return contractHash
    }
}
