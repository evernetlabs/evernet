from flask import g, request
from functools import wraps
import jwt


def required_param(key: str, data_type=str):
    if not g.request_body:
        raise Exception("Request body is missing")
    if key not in g.request_body:
        raise Exception(f"{key} is required")
    val = g.request_body[key]
    if not isinstance(val, data_type):
        raise Exception(f"Invalid data type for value of {key}")
    return val


def optional_param(key: str, data_type=str):
    if not g.request_body:
        return None
    if key not in g.request_body:
        return None
    val = g.request_body[key]
    if not isinstance(val, data_type):
        raise Exception(f"Invalid data type for value of {key}")
    return val


def pagination_page():
    return request.args.get("page", 0, int)


def pagination_size():
    return request.args.get("size", 50, int)


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
                g.config_service.get_jwt_signing_key(),
                algorithms=['HS256'],
                issuer=g.config_service.get_vertex_endpoint(),
                audience=g.config_service.get_vertex_endpoint(),
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

def authenticate_actor(should_be_local=False):
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
                    issuer=f"{issuer_vertex_endpoint}/{issuer_node_identifier}",
                    options={"verify_aud": False}
                )

                if data.get("type") != "actor":
                    raise Exception("Invalid access token type")

                target_vertex_endpoint = g.config_service.get_vertex_endpoint()

                target_node_address = data.get("aud")
                target_node_address_components = target_node_address.split("/")

                if len(target_node_address_components) != 2:
                    raise Exception("Invalid audience in access token")

                if target_node_address_components[0] != target_vertex_endpoint:
                    raise Exception("Invalid target vertex in access token")

                target_node_identifier = target_node_address_components[1]

                if not g.node_service.exists(target_node_identifier):
                    raise Exception("Invalid target node in access token")

                if should_be_local:
                    if target_node_identifier != issuer_node_identifier or target_vertex_endpoint != issuer_vertex_endpoint:
                        raise Exception("You are not allowed to perform this action")

                current_actor = {
                    "identifier": data["sub"],
                    "address": f"{issuer_vertex_endpoint}/{issuer_node_identifier}/{data['sub']}",
                    "source_vertex_endpoint": issuer_vertex_endpoint,
                    "source_node_identifier": issuer_node_identifier,
                    "source_node_address": f"{issuer_vertex_endpoint}/{issuer_node_identifier}",
                    "target_vertex_endpoint": target_vertex_endpoint,
                    "target_node_identifier": target_node_identifier,
                    "target_node_address": target_node_address,
                }

            except Exception as e:
                raise Exception(f"Invalid access token {str(e)}")

            return f(current_actor, *args, **kwargs)

        return decorated
    return decorator