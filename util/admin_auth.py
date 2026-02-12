from flask import request, g
import jwt
from functools import wraps

from exception.errors import AuthorizationError


def authenticate_user(f):
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
            vertex_endpoint = g.config_service.get_vertex_endpoint()
            data = jwt.decode(
                token,
                g.config_service.get_jwt_signing_key(),
                algorithms=['HS256'],
                issuer=vertex_endpoint,
                audience=vertex_endpoint
            )

            if data["type"] != "admin":
                raise Exception("Invalid access token")

            current_admin = {
                "username": data["sub"],
            }

        except Exception as e:
            raise AuthorizationError(f"Invalid access token, {str(e)}")

        return f(current_admin, *args, **kwargs)

    return decorated
