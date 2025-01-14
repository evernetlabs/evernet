from dotenv import load_dotenv
from flask import Flask, request, jsonify, g
import os
from vertex import Health, Info

load_dotenv()
jwt_signing_key = os.getenv("JWT_SIGNING_KEY")
vertex_endpoint = os.getenv("VERTEX_ENDPOINT")

app = Flask(__name__)

Health(app).register()
Info(app, os.getenv("VERTEX_NAME"), vertex_endpoint, os.getenv("VERTEX_DESCRIPTION")).register()

@app.before_request
def before_request():
    g.request_body = request.get_json(force=True, silent=True)
    g.jwt_signing_key = jwt_signing_key
    g.vertex_endpoint = vertex_endpoint


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
