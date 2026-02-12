from flask import g, request, Flask


def setup_api(app: Flask):
    @app.before_request
    def before_request():
        g.request_body = request.get_json(force=True, silent=True)


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


def page():
    return request.args.get("page", 0, int)


def size():
    return request.args.get("size", 50, int)

