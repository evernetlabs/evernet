import time
import uuid
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

        vertex_endpoint = g.config_service.get_vertex_endpoint()
        try:
            data = jwt.decode(
                token,
                g.config_service.get_jwt_signing_key(),
                algorithms=['HS256'],
                issuer=vertex_endpoint,
                audience=vertex_endpoint
            )

            if data["type"] != "admin":
                raise Exception("Invalid access token")

            current_user = {
                "username": data["sub"]
            }

        except Exception as e:
            print(e)
            raise Exception("Invalid access token")

        return f(current_user, *args, **kwargs)

    return decorated



def generate_admin_token(username: str) -> str:
    vertex_endpoint = g.config_service.get_vertex_endpoint()
    payload = {
        "sub": username,
        "type": "admin",
        "iss": vertex_endpoint,
        "aud": vertex_endpoint,
        "iat": int(time.time()),
        "exp": int(time.time()) + (60 * 60 * 24),
        "jti": str(uuid.uuid4())
    }

    token = jwt.encode(payload, g.config_service.get_jwt_signing_key(), algorithm='HS256')
    return token

def authenticate_user(must_be_local=True):
    def decorator(f):
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
                kid = jwt.get_unverified_header(token)["kid"]
                issuer_vertex_endpoint, issuer_node_identifier, issuer_signing_public_key = g.node_key_service.get_signing_public_key(kid)

                data = jwt.decode(
                    token,
                    issuer_signing_public_key,
                    algorithms=['EdDSA'],
                    issuer="%s/%s" % (issuer_vertex_endpoint, issuer_node_identifier),
                    options={"verify_aud": False}
                )

                if data["type"] != "user":
                    raise Exception("Invalid access token")

                audience = data["aud"]
                audience_components = str(audience).split("/")

                if len(audience_components) != 2:
                    raise Exception("Invalid audience in access token")

                audience_vertex_endpoint = audience_components[0]
                audience_node_identifier = audience_components[1]

                current_vertex_endpoint = g.config_service.get_vertex_endpoint()

                if audience_vertex_endpoint != current_vertex_endpoint:
                    raise Exception("Invalid audience in access token")

                is_local = audience_node_identifier == issuer_node_identifier and audience_vertex_endpoint == issuer_vertex_endpoint

                if must_be_local and not is_local:
                    raise Exception("User must be local to this node")

                current_user = {
                    "username": data["sub"],
                    "source_vertex_endpoint": issuer_vertex_endpoint,
                    "source_node_identifier": issuer_node_identifier,
                    "source_node_address": "%s/%s" % (issuer_vertex_endpoint, issuer_node_identifier),
                    "address": "%s/%s/%s" % (issuer_vertex_endpoint, issuer_node_identifier, data["sub"]),
                    "target_vertex_endpoint": audience_vertex_endpoint,
                    "target_node_identifier": audience_node_identifier,
                    "target_node_address": "%s/%s" % (audience_vertex_endpoint, audience_node_identifier),
                    "local": is_local
                }
            except Exception as e:
                print(e)
                raise Exception("Invalid access token")

            return f(current_user, *args, **kwargs)

        return decorated
    return decorator


def generate_user_token(current_node_identifier: str, username: str, target_node_address: str | None = None) -> str:
    current_vertex_endpoint = g.config_service.get_vertex_endpoint()
    issuer = "%s/%s" % (current_vertex_endpoint, current_node_identifier)

    if not target_node_address:
        target_node_address = issuer
    jwt_token = jwt.encode(
        payload={
            "sub": username,
            "exp": int(time.time() + 3600 * 24),
            "iat": int(time.time()),
            "iss": issuer,
            "aud": target_node_address,
            "jti": str(uuid.uuid4()),
            "type": "user"
        },
        headers={
            "kid": issuer
        },
        key=g.node_service.get_signing_private_key(current_node_identifier),
        algorithm="EdDSA"
    )

    return jwt_token