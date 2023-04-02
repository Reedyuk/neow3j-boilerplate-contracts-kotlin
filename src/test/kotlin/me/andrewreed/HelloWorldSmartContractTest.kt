package me.andrewreed

import io.neow3j.contract.SmartContract
import io.neow3j.test.ContractTest
import io.neow3j.test.ContractTestExtension
import io.neow3j.test.DeployConfig
import io.neow3j.test.DeployConfiguration
import io.neow3j.types.ContractParameter
import io.neow3j.types.Hash160
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.IOException

@ContractTest(blockTime = 1, contracts = [HelloWorldSmartContract::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
object HelloWorldSmartContractTest {

    private val GET_OWNER = "getOwner"
    private val GET_A_STRING = "getStaticValue"
    private val OWNER_ADDRESS = "NNSyinBZAr8HMhjj95MfkKD1PY7YWoDweR"

    @RegisterExtension
    private val ext = ContractTestExtension()

    private var contract: SmartContract? = null

    @BeforeAll
    fun setUp() {
        contract = ext.getDeployedContract(HelloWorldSmartContract::class.java)
    }

    @DeployConfig(HelloWorldSmartContract::class)
    fun configure(): DeployConfiguration? {
        val config = DeployConfiguration()
        val owner = ContractParameter.hash160(Hash160.fromAddress(OWNER_ADDRESS))
        config.setDeployParam(owner)
        config.setSubstitution("\${placeholder}", "A string value.")
        return config
    }

    @Test
    @Throws(IOException::class)
    fun invokeGetOwner() {
        val result = contract!!.callInvokeFunction(GET_OWNER)
        Assertions.assertEquals(result.invocationResult.stack[0].address, OWNER_ADDRESS)
    }

    @Test
    @Throws(IOException::class)
    fun invokeGetAString() {
        val result = contract!!.callInvokeFunction(GET_A_STRING)
        Assertions.assertEquals(result.invocationResult.stack[0].string, "simple test value")
    }

}