import os
import traceback

from dotenv import *
from flask import Flask, request, jsonify, g
from flask_cors import CORS

from mongita import MongitaClientDisk

from service.config_service import ConfigService
from service.admin_service import AdminService

from controller.health_check_controller import HealthCheckController
from controller.admin_controller import AdminController

app = Flask(__name__)
CORS(app)
load_dotenv()

mongo_client = MongitaClientDisk(host=os.getenv("DATA_DIR", "data")).vertex

config_service = ConfigService(mongo_client.configs)
admin_service = AdminService(mongo_client.admins, config_service)


HealthCheckController(app).register()
AdminController(app, admin_service).register()

@app.before_request
def before_request():
    g.request_body = request.get_json(force=True, silent=True)
    g.config_service = config_service


@app.errorhandler(404)
def handle_404_error(e):
    return jsonify({
        "success": False,
        "message": str(e)
    }), 404


@app.errorhandler(Exception)
def handle_all_errors(e):
    print(traceback.format_exc())
    return jsonify({
        "success": False,
        "message": str(e)
    }), 500


if __name__ == '__main__':
    app.run(host=os.getenv("HOST", "0.0.0.0"), port=int(os.getenv("PORT", "3000")), debug=os.getenv("ENV", "DEV") != "PROD")
