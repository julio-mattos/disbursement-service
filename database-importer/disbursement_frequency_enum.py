from enum import Enum


def get_frequency(frequency_str):
    return DisbursementFrequencyEnum[frequency_str.upper()]


class DisbursementFrequencyEnum(Enum):
    DAILY = "DAILY"
    WEEKLY = "WEEKLY"
