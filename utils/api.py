from flask import g, request

from exception.types import ClientException


def required_param(key: str, data_type=str):
    if not g.request_body:
        raise ClientException("Request body is missing")
    if key not in g.request_body:
        raise ClientException(f"{key} is required")
    val = g.request_body[key]
    if not isinstance(val, data_type):
        raise ClientException(f"Invalid data type for value of {key}")
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
        raise ClientException(f"Invalid data type for value of {key}")
    return val


def pagination_page():
    return request.args.get("page", 0, int)


def pagination_size():
    return request.args.get("size", 50, int)
