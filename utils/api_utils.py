from functools import wraps

import jwt
from flask import g, request


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
    if not val:
        return None
    if not isinstance(val, data_type):
        raise Exception(f"Invalid data type for value of {key}")
    return val


def pagination_page():
    return request.args.get("page", 0, int)


def pagination_size():
    return request.args.get("size", 50, int)


def authenticate_admin():
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
                data = jwt.decode(
                    token,
                    g.config_service.get_jwt_signing_key(),
                    algorithms=['HS256'],
                    issuer=g.config_service.get_vertex_endpoint(),
                    audience=g.config_service.get_vertex_endpoint()
                )

                if data["type"] != "admin":
                    raise Exception("Invalid access token")

                current_admin = {
                    "identifier": data["sub"]
                }

            except Exception as e:
                print(e)
                raise Exception("Invalid access token")

            return f(current_admin, *args, **kwargs)

        return decorated

    return decorator


def authenticate_actor(must_be_local=False):
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
                kid = jwt.get_unverified_header(token).get("kid")
                issuer_vertex_endpoint, issuer_node_identifier, issuer_signing_public_key = g.node_key_service.get_signing_public_key(kid)

                data = jwt.decode(
                    token,
                    issuer_signing_public_key,
                    algorithms=['EdDSA'],
                    issuer="%s/%s" % (issuer_vertex_endpoint, issuer_node_identifier),
                    options={"verify_aud": False}
                )

                if data.get("type") != "actor":
                    raise Exception("Invalid access token")

                audience = data.get("aud")
                audience_components = audience.split("/")

                if len(audience_components) != 2:
                    raise Exception("Invalid audience")

                current_vertex_endpoint = g.config_service.get_vertex_endpoint()
                audience_vertex_endpoint = audience_components[0]
                audience_node_identifier = audience_components[1]

                if current_vertex_endpoint != audience_vertex_endpoint:
                    raise Exception("Audience vertex endpoint does not match current vertex")

                if not g.node_service.exists(audience_node_identifier):
                    raise Exception("Audience node does not exist")

                if must_be_local:
                    if audience_vertex_endpoint != issuer_vertex_endpoint and audience_node_identifier != issuer_node_identifier:
                        raise Exception("Audience node is not local to the issuer vertex")

                current_actor = {
                    "identifier": data.get("sub"),
                    "address": "%s/%s/%s" % (issuer_vertex_endpoint, issuer_node_identifier, data.get("sub")),
                    "issuer_vertex_endpoint": issuer_vertex_endpoint,
                    "issuer_node_identifier": issuer_node_identifier,
                    "issuer_node_address": "%s/%s" % (issuer_vertex_endpoint, issuer_node_identifier),
                    "audience_node_identifier": audience_node_identifier,
                    "audience_node_address": "%s/%s" % (audience_vertex_endpoint, audience_node_identifier),
                }
            except Exception as _:
                raise Exception("Invalid access token")

            return f(current_actor, *args, **kwargs)

        return decorated
    return decorator
        