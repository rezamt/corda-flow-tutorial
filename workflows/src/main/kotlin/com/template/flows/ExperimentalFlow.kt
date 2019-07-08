package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.*
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.UntrustworthyData
import net.corda.core.utilities.unwrap

@StartableByRPC
@InitiatingFlow
class ExperimentalFlow : FlowLogic<Unit>() {
    companion object {
        object SENDING : ProgressTracker.Step("SendingData")
    }

    override val progressTracker = ProgressTracker(SENDING)

    @Suspendable
    override fun call() {


        val counterParty = serviceHub.identityService.wellKnownPartyFromX500Name(

                // O=PartyB,L=New York,C=US
                CordaX500Name(
                        organisation = "PartyB",
                        locality = "New York",
                        country = "US")) ?: throw IllegalStateException("Can't find Client party")


        val session = initiateFlow(counterParty)
        session.sendAndReceive<String>(42).unwrap {
            logger.info("ExperimentalFlow received $it")
        }
    }
}
/**
@InitiatedBy(ExperimentalFlow::class)
class ExperimentalFlowResponder(val counterParty: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val receivedValue = counterParty.receive<Int>().unwrap { it }
        logger.info("Responder Received $receivedValue")
        counterParty.send("Hey I am legal responder, you sent me $receivedValue which makes me happy")
    }

}
        **/

@InitiatedBy(ExperimentalFlow::class)
abstract class ExperimentalFlowResponder(val counterParty: FlowSession) : FlowLogic<Unit>()

@InitiatedBy(ExperimentalFlow::class)
open class IllegalExperimentalFlowResponder(counterParty: FlowSession) : ExperimentalFlowResponder(counterParty) {
    @Suspendable
    override fun call() {
        val receivedValue = counterParty.receive<Int>().unwrap { it }
        logger.info("Responder Received $receivedValue")
        counterParty.send("Hey I am ILLEGAL, you sent me $receivedValue and I will try to HACK")
    }

}