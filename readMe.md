# EquiFarm

- Swagger UI `http://localhost:8082/swagger-ui/index.html`

## Escrow Feature

### Trade

- Status (pending, released, disputed)
- sender(Wallet)
- receiver(Wallet)
- amount
- currency
- is_opened
- Release_date

### Dispute

- complaint
- initiation_date
- resolution_date
- resolution_decision
- status(open,under_review, resolved)
- initiator
- Escrow_wallet



"""
I find with financial transactions you want to have a detailed audit trail of every movement of funds. This means a write-once (no updates) table containing fund movements using a double entry accounting model.

In a simple version of this, you would have one table containing the list of payees and payers (your senders, administrators and receivers). Then you would have another table (or better two) that contain the financial transactions. The tables are roughly as follows:

PARTY: Name, address, bank account, other details about payers/payees.

TRANSACTION: Date, transaction ID, order reference, other general info.

TRANSACTION-DETAIL: FK to Transaction, FK to party, Amount.

When someone makes a simple payment you record one TRANSACTION and two TRANSACTION-DETAIL records. The first record shows who paid and the second shows who was paid. The sum of the amounts for the two details must be zero, that means you need to pick a convention for positive and negative Amounts such that one means giving money and the other means getting money.

If the transaction is more complicated, you just add more TRANSACTION-DETAIL records, for example instead of one person getting paid, you get two people being paid (administrator and seller for example).

While the sum of transaction detail amounts for any transaction is zero, the sum of amounts for any given PARTY may be non-zero, meaning you have some of their money in your system. To clear these amounts, you need a PARTY representing any payment settlement organization or bank that you deal with. These are the ultimate sources and sinks of funds and their balances don't need to be zero in the long run.

All of this may seem complicated, especially compared to what you were thinking of, but the advantage of it is that it allows you to keep a detailed record of exactly what happened and when it happened. If you need to make an update, i.e. cancel and order or issue a credit note, just add more transactions and transaction details. You can always go back and see exactly what went on. This convention is how people have been handling and accounting for financial transactions for hundreds of years, so if nothing else, people will understand what you're doing.

Here is an example with some data. In this example, Buyer X pays 100.00 for an order. The Agency collects 10.00 and pays Seller 1 40.00 and Seller 2 50.00. They buyer pays with PayPal and the Agency and both Sellers deposit their money in one of two banks. Note that this example is quite simple insofar as the agency and the sellers clear out their accounts after just one order. In a real-world scenario, payments for many orders might be batched up before settlement is made.

Sample Data

Note that for OP's scenario where determination of the seller may be delayed, the agency would need to have some kind of escrow account. This would be an additional party, such as "Outstanding Order Payments". In such a case the agency could take their fee right away and the balance would be placed in the escrow account. When the sellers are determined later on, they would be paid out of the escrow account, rather than out of the buyer's account, as is shown in the example, above.

Also note that while each transaction is shown with two details above, it is also legitimate to have three or more details for one transaction if you happen to want to record it that way. For example, all of the details in 8001, 8002 and 8003 could have been represented as a single transaction if you prefer.
"""
