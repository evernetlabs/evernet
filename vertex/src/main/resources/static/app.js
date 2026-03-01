$.ajaxSetup({
    statusCode: {
        401: function(jqxhr, textStatus, errorThrown) {
            localStorage.clear()
            window.location.href = "/login"
        }
    }
})

function displayError(parent, message) {
    parent.html(`<div class="alert alert-danger">${message}</div>`);
}

function displaySuccess(parent, message) {
    parent.html(`<div class="alert alert-success">${message}</div>`);
}

function getHeaders() {
    return {
        Authorization: `Bearer ${localStorage.getItem('token')}`,
    }
}
