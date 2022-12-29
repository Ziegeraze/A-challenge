# adidas NodeJS Code Challenge

You are tasked with implementing a payment authorization module which should validate a set of predefined rules. Please read the instructions below, and feel free to ask for clarifications if needed.

## Inputs

Your program will receive (as an input) a set of json lines from `stdin` and should write a json line in `stdout` for each one — imagine this as a stream of events arriving at the authorizer.

```bash
$ cat operations
{"payment-rules": {"max-limit": 100}}
{"payment-session": {"payment-id": 89087653}}
{"payment-session": {"payment-id": 89087653, "cc": "visa", "amount": 70, "time": "2022-02-13T10:00:00.000Z"}}
{"payment-session": {"payment-id": 89087653, "cc": "mastercard", "amount": 40, "time": "2022-02-13T11:00:00.000Z"}}

$ authorize < operations
{"payment-rules": {"limit": 100}}
{"payment-session": {"payment-id": 89087653, "available-limit": 100}, "violations": []}
{"payment-session": {"payment-id": 89087653, "available-limit": 30}, "violations": []}
{"payment-session": {"payment-id": 89087653, "available-limit": 30}, "violations": ["insufficient-limit"]}
```

## State Management

The program should not rely on any external database or datastore. The internal state should be handled by an explicit in-memory structure. The state is to be reset at the application start.

*NOTE: Use an immutable data structure is a must.*

## Operations

The program handles three types of operations, deciding on which one according to the line that is being processed:
1. Global Payment rules set-up
2. Payment session creation
3. Payment authorization

*NOTE: For the sake of simplicity, you can assume all monetary values are positive integers using a currency without cents*

### Type of Operations

**1. Global Payment rules set-up**

`Input`

Set up the rules for all payment sessions. Currently, the only rule supported is the `max-limit` for a payment session.

`Output`

Global rules were created.

`Business Rules`

The latest global rules are the only valid for the system

```bash
-- input
{"payment-rules": {"max-limit": 100}}
{"payment-rules": {"max-limit": 200}}

-- output
{"payment-rules": {"max-limit": 100}}
{"payment-rules": {"max-limit": 200}}
```

**2.Payments Session Creation**

`Input`

Creates the payment session with a `payment-id`. Multiple payments sessions are supported currently

`Output`

The created account's current state plus any business logic violations.

`Business rules`

Once created, the payment session not be updated or recreated: `payment-session-already-initialized`.

```bash
-- input
{"payment-rules": {"max-limit": 100}}
{"payment-session": {"payment-id": 89087653}}
{"payment-session": {"payment-id": 90800543}}
{"payment-session": {"payment-id": 89087653}}

-- output
{"payment-session": {"payment-id": 89087653, "available-limit": 100}, "violations": []}
{"payment-session": {"payment-id": 90800543, "available-limit": 100}, "violations": []}
{"payment-session": {"payment-id": 89087653, "available-limit": 100}, "violations": ["payment-session-already-initialized"]}
```

**3.Payments authorization**

`Input`

Tries to authorize a payment for a particular session, credit card type and transaction time.

`Output`

The payment item state plus any business logic violations.

`Business rules`

You should implement the following rules, keeping in mind new rules will appear in the future:

* No payments should be accepted without a properly rules initialization: `paymentrules-not-initialized`
* No payments should be accepted without a properly initialized payment:
  `paymentsession-not-initialized`
* The transaction amount should not exceed the global limit: `insufficient-limit`
* There should not be more than 3 payments on a 2-minute interval:
  `high-frequency-small-interval` (the input order cannot be relied upon since
  transactions can eventually be out of order respectively to their times)
* There should not be more than 1 similar transactions (same amount and credit card) in a
  2 minutes interval: `doubled-transaction`

### Examples

```bash
-- input
{"payment-session": {"payment-id": 89087653, "cc": "visa", "amount": 70, "time": "2022-02-13T10:00:00.000Z"}}
-- output
{"payment-session": {"payment-id": 89087653, "available-limit": 0}, "violations": ["paymentrules-not-initialized","paymentsession-not-initialized"]}
```

```bash
-- input
{"payment-rules": {"max-limit": 100}}
{"payment-session": {"payment-id": 89087653}}
{"payment-session": {"payment-id": 89087653, "cc": "visa", "amount": 120, "time": "2022-02-13T10:00:00.000Z"}}
-- output
{"payment-rules": {"max-limit": 100}}
{"payment-session": {"payment-id": 89087653}}
{"payment-session": {"payment-id": 89087653, "available-limit": 0}, "violations": ["insufficient-limit"]}
```

### Error Handling

Please assume input parsing errors will not happen. We will not evaluate your submission against input that breaks the contract.

Violations of the business rules are not considered to be errors as they are expected to happen and should be listed in the outputs' violations field as described on the output schema in the examples. That means the program execution should continue normally after any violation.

## Deliveries

* A Node JS project with the solution
* Using TypeScript is a MUST
* The project should be versioned in some Code Control System (GitHub)
* Building and running the application must be possible under Unix systems. Dockerized Builds are welcome.
* Utilize Functional Programming Principles is welcome
* You can use OOS Libraries to support your solution
 

