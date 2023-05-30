import datetime

from bson import Decimal128


class Order:

    def __init__(self,merchant_reference=None, status=None, amount=None, created_at=None ):
        self.merchant_reference = merchant_reference
        self.status = status
        self.amount = amount
        self.created_at = created_at

    def set_merchant_reference(self, merchant_reference):
        self.merchant_reference = merchant_reference

    def set_status(self, status):
        self.status = status

    def set_amount(self, amount):
        self.amount = Decimal128(amount)

    def set_created_at(self, created_at):
        self.created_at = datetime.datetime.strptime(created_at, "%d/%m/%Y").date().isoformat()

    def to_dict(self):
        return vars(self)
