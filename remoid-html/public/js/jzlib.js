function getSchemaDisplay(schema) {
    var display = [];

    angular.forEach(schema, function(value, key) {
        if (!isPromiseOrResolved(key)) {
            var obj = {};

            obj['name'] = key;
            obj['displayOrder'] = value.displayOrder;
            obj['displayName'] = value.displayName;

            display.push(obj);
        }
    });

    return display;
}

function isPromiseOrResolved(key) {
    if (key == '$promise' || key == '$resolved') {
        return true;
    }

    return false;
}

function bindCharsRemaining(characterLimit, messageElement, sourceText) {
    $(messageElement).html(characterLimit + " characters remaining.");

    $(sourceText).bind('keyup', function() {
        var charactersUsed = $(this).val().length;

        if (charactersUsed > characterLimit) {
            charactersUsed = characterLimit;
        }

        var charactersRemaining = characterLimit - charactersUsed;

        $(messageElement).html(charactersRemaining + " characters remaining.");
    });
}

function bindCharsRequired(characterLimit, messageElement, sourceText) {
    $(messageElement).html(characterLimit + " characters required.");

    $(sourceText).bind('keyup', function() {
        var charactersUsed = $(this).val().length;

        if (charactersUsed < characterLimit - 1) {
            $(messageElement).html(characterLimit - charactersUsed + " characters required.");
        } else if (charactersUsed < characterLimit) {
            $(messageElement).html(characterLimit - charactersUsed + " character required.");
        } else {
            $(messageElement).html("");
        }
    });
}
