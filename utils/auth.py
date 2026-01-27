from functools import wraps

import jwt
from flask import g, request

from exception.types import AuthenticationException, AuthorizationException


def authenticate_admin(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = None

        if 'Authorization' in request.headers:
            auth_header = request.headers['Authorization']
            if auth_header.startswith('Bearer '):
                token = auth_header.split(' ')[1]

        if not token:
            raise AuthenticationException("Invalid access token")

        vertex_endpoint = g.config_service.get_vertex_endpoint()
        jwt_signing_key = g.config_service.get_jwt_signing_key()

        try:
            data = jwt.decode(
                token,
                jwt_signing_key,
                algorithms=['HS256'],
                issuer=vertex_endpoint,
                audience=vertex_endpoint
            )

            if data["type"] != "admin":
                raise AuthenticationException("Invalid access token")

            current_admin = {
                "identifier": data["sub"],
            }

        except Exception as e:
            print(e)
            raise AuthenticationException("Invalid access token")

        return f(current_admin, *args, **kwargs)

    return decorated

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
                raise AuthenticationException("Invalid access token")

            try:
                kid = jwt.get_unverified_header(token).get("kid")
                issuer_vertex_endpoint, issuer_node_identifier, issuer_signing_public_key = g.node_key_service.get_signing_public_key(kid)

                data = jwt.decode(
                    token,
                    key=issuer_signing_public_key,
                    algorithms=['EdDSA'],
                    issuer="%s/%s" % (issuer_vertex_endpoint, issuer_node_identifier),
                    options={"verify_aud": False}
                )

                if data["type"] != "actor":
                    raise AuthenticationException("Invalid access token")

                audience = data["aud"]
                audience_components = str(audience).split("/")

                if len(audience_components) != 2:
                    raise AuthenticationException("Invalid audience in access token")

                audience_vertex_endpoint = audience_components[0]
                audience_node_identifier = audience_components[1]

                current_vertex_endpoint = g.config_service.get_vertex_endpoint()

                if audience_vertex_endpoint != current_vertex_endpoint:
                    raise AuthenticationException("Invalid audience in access token")

                if not g.node_service.exists(audience_node_identifier):
                    raise AuthenticationException("Invalid audience in access token")

                is_local = audience_node_identifier == issuer_node_identifier and audience_vertex_endpoint == issuer_vertex_endpoint

                if must_be_local and not is_local:
                    raise AuthorizationException("Access token does not belong to a local actor")

                current_actor = {
                    "identifier": data["sub"],
                    "issuer_vertex_endpoint": issuer_vertex_endpoint,
                    "issuer_node_identifier": issuer_node_identifier,
                    "issuer_node_address": "%s/%s" % (issuer_vertex_endpoint, issuer_node_identifier),
                    "address": "%s/%s/%s" % (issuer_vertex_endpoint, issuer_node_identifier, data["sub"]),
                    "audience_vertex_endpoint": audience_vertex_endpoint,
                    "audience_node_identifier": audience_node_identifier,
                    "audience_node_address": "%s/%s" % (audience_vertex_endpoint, audience_node_identifier),
                    "local": is_local
                }
            except Exception as e:
                print(e)
                raise AuthenticationException("Invalid access token")

            return f(current_actor, *args, **kwargs)

        return decorated
    return decorator