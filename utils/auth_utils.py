from flask import request, g
from functools import wraps
import jwt


def authenticate_admin(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = None

        if 'Authorization' in request.headers:
            auth_header = request.headers['Authorization']
            if auth_header.startswith('Bearer '):
                token = auth_header.split(' ')[1]

        if not token:
            raise Exception("Invalid access token")

        try:
            data = jwt.decode(
                token,
                g.config_manager.get_jwt_signing_key(),
                algorithms=['HS256'],
                issuer=g.config_manager.get_vertex_endpoint(),
                audience=g.config_manager.get_vertex_endpoint()
            )

            if data["type"] != "admin":
                raise Exception("Invalid access token")

            current_admin = {
                "identifier": data["sub"]
            }

        except Exception as _:
            raise Exception("Invalid access token")

        return f(current_admin, *args, **kwargs)

    return decorated
