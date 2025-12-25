import os
import traceback

from dotenv import *
from flask import Flask, config, request, jsonify, g
from mongita import MongitaClientDisk

from api.admin_api import AdminAPI
from api.health_check_api import HealthCheckAPI
from service.admin_service import AdminService
from service.config_service import ConfigService

load_dotenv()

db_client = MongitaClientDisk(host=os.getenv("DATA_PATH", "data"))
db = db_client.vertex

app = Flask(__name__)

config_service = ConfigService(db.configs)
admin_service = AdminService(db.admins, config_service)

HealthCheckAPI(app).register()
AdminAPI(app, admin_service).register()

@app.before_request
def before_request():
    g.request_body = request.get_json(force=True, silent=True)
    g.config_service = config_service


@app.errorhandler(404)
def handle_404_error(e):
    return jsonify({
        "success": False,
        "error": str(e)
    }), 404


@app.errorhandler(Exception)
def handle_all_errors(e):
    print(traceback.format_exc())
    return jsonify({
        "success": False,
        "error": str(e)
    }), 500


if __name__ == '__main__':
    app.run(host=os.getenv("HOST"), port=int(os.getenv("PORT")), debug=os.getenv("ENV") != "PROD")
