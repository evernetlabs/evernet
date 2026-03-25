from flask import g, request
from functools import wraps


def required_param(key: str, data_type=str):
    if not g.request_body:
        raise Exception("Request body is missing")
    if key not in g.request_body:
        raise Exception(f"{key} is required")
    val = g.request_body[key]
    if data_type is not None:
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
    if data_type is not None:
        if not isinstance(val, data_type):
            raise Exception(f"Invalid data type for value of {key}")
    return val


def pagination_page():
    return request.args.get("page", 0, int)


def pagination_size():
    return request.args.get("size", 50, int)

def control_plane_api(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        if not g.control_plane:
            raise Exception("You are not in the control plane")
        return f(*args, **kwargs)
    return decorated
