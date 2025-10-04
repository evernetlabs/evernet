import os
import plyvel

from dotenv import *
from flask import Flask, request, jsonify, g

from vertex.config.admin.admin_api import AdminApi
from vertex.config.admin.admin_manager import AdminManager
from vertex.config.config_manager import ConfigManager
from vertex.health.health_api import HealthApi

load_dotenv()
data_path = os.getenv("DATA_PATH")

print("initializing data path...")
if not os.path.exists(data_path):
    os.makedirs(data_path)

print("initializing database...")
if not os.environ.get("WERKZEUG_RUN_MAIN"):
    print("Skipping DB init in reloader process")
    db = None
else:
    db = plyvel.DB(f'{data_path}/evernet.db', create_if_missing=True)

app = Flask(__name__)

config_manager = ConfigManager(db)
admin_manager = AdminManager(db, config_manager)

HealthApi(app).register()
AdminApi(app, admin_manager).register()


@app.before_request
def before_request():
    g.request_body = request.get_json(force=True, silent=True)


@app.errorhandler(404)
def handle_404_error(e):
    return jsonify({
        "success": False,
        "message": str(e)
    }), 404


@app.errorhandler(Exception)
def handle_all_errors(e):
    return jsonify({
        "success": False,
        "message": str(e)
    }), 500


if __name__ == '__main__':
    app.run(host=os.getenv("HOST"), port=int(os.getenv("PORT")), debug=os.getenv("ENV") != "PROD")
