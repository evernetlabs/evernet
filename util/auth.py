from functools import wraps
from flask import g, request
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
                g.jwt_signing_key,
                algorithms=['HS256'],
                issuer='evernet',
                audience='evernet'
            )

            if data["type"] != "admin":
                raise Exception("Invalid access token")

            current_admin = {
                "identifier": data["sub"]
            }

        except Exception as e:
            print(e)
            raise Exception("Invalid access token")

        if admin and not current_admin["admin"]:
            raise Exception("You are not allowed to perform this action")

        return f(current_admin, *args, **kwargs)

    return decorated

def generate_admin_token(identifier: str) -> str:
    payload = {
        "sub": identifier,
        "type": "admin",
        "iss": "evernet",
        "aud": "evernet",
    }

    token = jwt.encode(payload, g.jwt_signing_key, algorithm='HS256')
    return token
