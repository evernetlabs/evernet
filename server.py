import os
import traceback

import pymongo
from dotenv import *
from flask import Flask, request, jsonify, g

from admin.admin_api import AdminAPI
from admin.admin_service import AdminService
from config.config_service import ConfigService
from health.health_api import HealthAPI
from node.node_api import NodeAPI
from node.node_service import NodeService
from vertex.vertex_api import VertexAPI
from vertex.vertex_service import VertexService

app = Flask(__name__)
load_dotenv()

jwt_signing_key = os.getenv("JWT_SIGNING_KEY")
db = pymongo.MongoClient(os.getenv("DB_HOST"), int(os.getenv("DB_PORT"))).evernet

config_service = ConfigService(db.configs)
vertex_service = VertexService(config_service)
admin_service = AdminService(db.admins, config_service)
node_service = NodeService(db.nodes)

HealthAPI(app).register()
VertexAPI(app, vertex_service).register()
AdminAPI(app, admin_service).register()
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
    app.run(host=os.getenv("HOST"), port=int(os.getenv("PORT")), debug=os.getenv("ENV") != "PROD")
