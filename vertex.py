import os

from flask import Flask, request
from montydb import set_storage, MontyClient

from api.admin_api import AdminAPI
from api.health_check_api import HealthCheckAPI
from exception.flask_handler import register_flask_exception_handler
from service.admin_service import AdminService
from service.config_service import ConfigService
from util.api import setup_api

data_dir = os.getenv("DATA_PATH", "data")
set_storage(data_dir, storage="lightning")
database_client = MontyClient(data_dir)
database = database_client.get_database(os.getenv("DATABASE_NAME", "vertex"))

app = Flask(__name__)
register_flask_exception_handler(app)
setup_api(app)

config_service = ConfigService(database.get_collection("configs"))
admin_service = AdminService(database.get_collection("admins"), config_service)

HealthCheckAPI(app).register()
AdminAPI(app, admin_service).register()

if __name__ == '__main__':
    app.run(
        debug=os.getenv("DEBUG", "true") == "true",
        host=os.getenv("HOST", "0.0.0.0"),
        port=int(os.getenv("PORT", "8080"))
    )
