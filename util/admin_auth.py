from flask import request, g
import jwt
from functools import wraps

from exception.errors import AuthorizationError, AuthenticationError


def authenticate_admin(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = None

        if 'Authorization' in request.headers:
            auth_header = request.headers['Authorization']
            if auth_header.startswith('Bearer '):
                token = auth_header.split(' ')[1]

        if not token:
            raise AuthenticationError("Invalid access token")

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
                raise AuthenticationError("Invalid access token")

            current_admin = {
                "username": data["sub"],
            }

        except Exception as e:
            raise AuthenticationError(f"Invalid access token, {str(e)}")

        return f(current_admin, *args, **kwargs)

    return decorated
