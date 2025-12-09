import os
from platform import node
import traceback

from dotenv import *
from flask import Flask, request, jsonify, g
from flask_cors import CORS

from mongita import MongitaClientDisk

from service.actor_service import ActorService
from service.config_service import ConfigService
from service.admin_service import AdminService
from service.node_key_service import NodeKeyService
from service.node_service import NodeService
from service.actor_service import ActorService
from service.remote_node_service import RemoteNodeService
from service.structure_service import StructureService

from controller.config_controller import ConfigController
from controller.health_check_controller import HealthCheckController
from controller.admin_controller import AdminController
from controller.node_controller import NodeController
from controller.actor_controller import ActorController
from controller.structure_controller import StructureController

app = Flask(__name__)
CORS(app)
load_dotenv()

mongo_client = MongitaClientDisk(host=os.getenv("DATA_DIR", "data")).vertex

config_service = ConfigService(mongo_client.configs)
admin_service = AdminService(mongo_client.admins, config_service)
node_service = NodeService(mongo_client.nodes)
remote_node_service = RemoteNodeService()
node_key_service = NodeKeyService(node_service, remote_node_service, config_service)
actor_service = ActorService(mongo_client.actors, node_service, config_service)
structure_service = StructureService(mongo_client.structures, node_service, config_service)


HealthCheckController(app).register()
AdminController(app, admin_service).register()
ConfigController(app, config_service).register()
NodeController(app, node_service).register()
ActorController(app, actor_service).register()
StructureController(app, structure_service).register()

@app.before_request
def before_request():
    g.request_body = request.get_json(force=True, silent=True)
    g.config_service = config_service
    g.node_key_service = node_key_service
    g.node_service = node_service


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
