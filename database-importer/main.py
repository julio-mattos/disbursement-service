import importer

if __name__ == '__main__':
    csv_orders = 'orders.csv'
    csv_merchants = 'merchants.csv'

    importer.import_orders(csv_orders)
    importer.import_merchants(csv_merchants)



