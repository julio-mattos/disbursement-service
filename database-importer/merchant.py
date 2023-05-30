import datetime
from bson import Decimal128


class Merchant:

    def __init__(self, id=None, email=None, live_on=None, disbursement_frequency=None, minimum_monthly_fee=None):
        self._id = id
        self.email = email
        self.live_on = live_on
        self.disbursement_frequency = disbursement_frequency
        self.minimum_monthly_fee = minimum_monthly_fee

    def set_id(self, id):
        self._id = id

    def set_email(self, email):
        self.email = email

    def set_live_on(self, live_on):
        self.live_on = datetime.datetime.strptime(live_on, "%Y-%m-%d").date().isoformat()

    def set_disbursement_frequency(self, disbursement_frequency):
        self.disbursement_frequency = disbursement_frequency

    def set_minimum_monthly_fee(self, minimum_monthly_fee):
        self.minimum_monthly_fee = Decimal128(minimum_monthly_fee)

    def to_dict(self):
        return vars(self)
