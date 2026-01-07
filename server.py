import os
import traceback

import pymongo
from montydb import set_storage, MontyClient
from dotenv import load_dotenv
from flask import Flask, request, jsonify, g
from flask_cors import CORS

from api.config_api import ConfigAPI
from api.health_check_api import HealthCheckAPI
from api.admin_api import AdminAPI
from api.node_api import NodeAPI
from api.vertex_api import VertexAPI
from service.admin_service import AdminService
from service.config_service import ConfigService
from service.node_service import NodeService
from service.vertex_service import VertexService

app = Flask(__name__)
CORS(app)
load_dotenv()

embedded_db = os.getenv("EMBEDDED_DB", "true").lower() == "true"
if embedded_db:
    repo = os.getenv("EMBEDDED_DB_PATH", "data")
    set_storage(repo, storage="lightning")
    db = MontyClient(repo).evernet
else:
    db = pymongo.MongoClient(os.getenv("DB_HOST", "localhost"), int(os.getenv("DB_PORT", "27017"))).evernet

config_service = ConfigService(db.configs)
admin_service = AdminService(db.admins, config_service)
vertex_service = VertexService(config_service)
node_service = NodeService(db.nodes)

HealthCheckAPI(app).register()
AdminAPI(app, admin_service).register()
VertexAPI(app, vertex_service).register()
ConfigAPI(app, config_service).register()
NodeAPI(app, node_service).register()


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
    app.run(
        host=os.getenv("HOST", "0.0.0.0"),
        port=int(os.getenv("PORT", "5000")),
        debug=os.getenv("ENV", "DEV") != "PROD"
    )
