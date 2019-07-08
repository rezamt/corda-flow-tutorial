package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.UntrustworthyData
import net.corda.core.utilities.unwrap

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class FlowA (val recipient: Party) : FlowLogic<Unit>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call() {
        subFlow(FlowB(recipient))
    }
}


// Note: No annotations. This is used as an inlined subflow.
class FlowB(val recipient: Party) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val message = "I'm an inlined subflow, so I inherit the @InitiatingFlow's session ID and type."
        initiateFlow(recipient).send(message)
    }
}



@InitiatedBy(FlowA::class)
class FlowAResponder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        // Responder flow logic goes here.

        val otherFlowVersion = counterpartySession.getCounterpartyFlowInfo().flowVersion
        // val receivedString = counterpartySession.receive<String>()

        val packet1: UntrustworthyData<String> = counterpartySession.receive<String>()
        val receivedString: String = packet1.unwrap { data ->
            // Perform checking on the object received.
            // T O D O: Check the received object.
            // Return the object.
            data
        }


        logger.info("FlowAResponder >>>> "+ otherFlowVersion + receivedString);

    }
}



@InitiatingFlow
@StartableByRPC
class Initiator : FlowLogic<Unit>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call() {
        // Initiator flow logic goes here.

    }
}

@InitiatedBy(Initiator::class)
class Responder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        // Responder flow logic goes here.
    }
}
