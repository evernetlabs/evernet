import os
import traceback

import pymongo
from dotenv import load_dotenv
from flask import Flask, request, jsonify, g
from flask_cors import CORS

from api.admin_api import AdminAPI
from api.config_api import ConfigAPI
from api.health_check_api import HealthCheckAPI
from api.node_api import NodeAPI
from api.structure_api import StructureAPI
from api.user_api import UserAPI
from service.admin_service import AdminService
from service.config_service import ConfigService
from service.node_key_service import NodeKeyService
from service.node_service import NodeService
from service.remote_node_service import RemoteNodeService
from service.structure_service import StructureService
from service.user_service import UserService

app = Flask(__name__)
CORS(app)
load_dotenv()

db = pymongo.MongoClient(os.getenv("DB_HOST", "localhost"), int(os.getenv("DB_PORT", "27017"))).evernet

config_service = ConfigService(db.configs)
admin_service = AdminService(db.admins, config_service)
node_service = NodeService(db.nodes)
remote_node_service = RemoteNodeService(config_service)
node_key_service = NodeKeyService(config_service, node_service, remote_node_service)
user_service = UserService(db.users, node_service)
structure_service = StructureService(db.structures, node_service, config_service)

HealthCheckAPI(app).register()
AdminAPI(app, admin_service).register()
ConfigAPI(app, config_service).register()
NodeAPI(app, node_service).register()
UserAPI(app, user_service).register()
StructureAPI(app, structure_service).register()


@app.before_request
def before_request():
    g.request_body = request.get_json(force=True, silent=True)
    g.config_service = config_service
    g.node_service = node_service
    g.node_key_service = node_key_service


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
    app.run(host=os.getenv("HOST"), port=int(os.getenv("PORT", "8080")), debug=os.getenv("ENV", "DEV") != "PROD")
