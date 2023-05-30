import csv
from pymongo import MongoClient

from merchant import Merchant
from orders import Order

mongo_url = "mongodb://localhost:27017/"
database = "sequra"


def import_orders(csv_file_path):
    with open(csv_file_path, 'r') as csv_file:
        csv_data = csv.DictReader(csv_file)
        test = list(csv_data)

        client = MongoClient(mongo_url)

        db = client[database]
        collection = db["order"]

        list_orders = []

        print("Starting import orders...")

        for t in test:
            body = t.get("merchant_reference;status;amount;created_at").split(";")
            order = Order()
            order.set_merchant_reference(body[0])
            order.set_status(body[1])
            order.set_amount(body[2])
            order.set_created_at(body[3])
            list_orders.append(order.to_dict())

        collection.insert_many(list_orders)

        print("{} orders imported.".format(len(list_orders)), )


def import_merchants(csv_file_path):
    with open(csv_file_path, 'r') as csv_file:
        csv_data = csv.DictReader(csv_file)
        object_list = list(csv_data)

        client = MongoClient(mongo_url)

        db = client[database]
        collection = db["merchant"]

        list_merchants = []

        print("Starting import merchant...")

        for i in object_list:
            body = i.get("reference;email;live_on;disbursement_frequency;minimum_monthly_fee").split(";")
            merchant = Merchant()
            merchant.set_id(body[0])
            merchant.set_email(body[1])
            merchant.set_live_on(body[2])
            merchant.set_disbursement_frequency(body[3])
            merchant.set_minimum_monthly_fee(body[4])
            list_merchants.append(merchant.to_dict())

        print("{} merchants imported.".format(len(list_merchants)))

        collection.insert_many(list_merchants)
