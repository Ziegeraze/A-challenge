package com.jpforero.challenge;

import org.springframework.boot.jackson.JsonComponent;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.Map;

import static com.jpforero.challenge.Constants.*;

@JsonComponent(PAYMENT_SESSION)
public class PaymentSessionAuthorization extends PaymentSession {

    private String cc;
    private int amount;
    private String time;

    public String getCc() {
        return cc;
    }

    public String getTime() {
        return time;
    }

    public int getAmount() {
        return amount;
    }

    public void validateSessionExist(Map<Integer, PaymentSession> paymentSessionMap) {
        if (!paymentSessionMap.containsKey(this.getPaymentId())) {
            this.setViolations(PAYMENT_SESSION_NOT_INITIALIZED);
        }
    }

    public void validateLimit(Map<Integer, PaymentSession> paymentSession) {
        if (!paymentSession.isEmpty() && this.amount > paymentSession.get(this.getPaymentId()).getAvailableLimit()) {
            this.setViolations(INSUFFICIENT_LIMIT);
        }
    }

    public void validateHighFrequencySmallInterval(LinkedList<PaymentSessionAuthorization> paymentSessionsAuthorizations) {
        if (paymentSessionsAuthorizations.size() >= 3) {
            int size = paymentSessionsAuthorizations.size();
            PaymentSessionAuthorization oldAuthorization = paymentSessionsAuthorizations.get(size - 3);

            Instant firstInstant = Instant.parse(oldAuthorization.getTime());
            Instant lastInstant = Instant.parse(this.getTime());

            long diff = firstInstant.until(lastInstant, ChronoUnit.SECONDS);

            if (diff <= THREE_MINUTES_IN_SECONDS) {
                this.setViolations(HIGH_FREQUENCY_SMALL_INTERVAL);
            }
        }
    }

    public void validateDoubledTransaction(LinkedList<PaymentSessionAuthorization> paymentSessionsAuthorizations) {
        if (!paymentSessionsAuthorizations.isEmpty()) {
            for (PaymentSessionAuthorization p : paymentSessionsAuthorizations) {
                Instant firstInstant = Instant.parse(p.getTime());
                Instant lastInstant = Instant.parse(this.getTime());

                long diff = firstInstant.until(lastInstant, ChronoUnit.SECONDS);

                if (diff < TWO_MINUTES_IN_SECONDS && p.getAmount() == this.amount && p.getCc().equals(this.cc)) {
                    this.setViolations(DOUBLED_TRANSACTION);
                }
            }
        }
    }

    public void updateLimit(Map<Integer, PaymentSession> paymentSessions) {
        if (!paymentSessions.isEmpty() && !hasViolations()) {
            int oldLimit = paymentSessions.get(this.getPaymentId()).getAvailableLimit();
            int newLimit = oldLimit - this.getAmount();

            paymentSessions.get(this.getPaymentId()).setAvailableLimit(newLimit);
        }
    }

    public void storeAuthorization(LinkedList<PaymentSessionAuthorization> paymentSessionsAuthorizations) {
        if (!hasViolations()) {
            paymentSessionsAuthorizations.addFirst(this);
        }
    }


    public void sessionAuthValidations(PaymentRules paymentRules, Map<Integer, PaymentSession> paymentSessions, LinkedList<PaymentSessionAuthorization> paymentSessionsAuthorizations) {
        validatePaymentRules(paymentRules);
        validateSessionExist(paymentSessions);
        validateLimit(paymentSessions);
        validateHighFrequencySmallInterval(paymentSessionsAuthorizations);
        validateDoubledTransaction(paymentSessionsAuthorizations);
        updateLimit(paymentSessions);
        storeAuthorization(paymentSessionsAuthorizations);
    }
}
