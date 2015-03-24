/**
 * Created by pi1 on 17/03/2015.
 */
$(document).ready(function () {

    var $samples = $('#jqp-samples'),
        typesQuery, sizesQuery;

    $samples.pager({
        url: '/samples/json/',
        template: '#sample-template',
        loadButton: '#jqp-load-more-samples',
        appendBefore: '#jqp-append-before-samples',
        scrollLoad: true
    });

    $samples.selectableElement({
        element: 'tbody .selectable',
        singleType: true
    });

    $('#create-group').group({
        modal: '#groupModal',
        confirm: '#modalConfirm',
        template: '#modal-sample-template',
        mode: 'samples'
    });

    $('#create-labware').group({
        modal: '#labwareModal',
        confirm: '#labwareModalConfirm',
        template: '#modal-sample-template',
        mode: 'samples',
        action: '/labware/create'
    });

    $samples.on('element.selected', function (event) {
        var $buttons = $('#create-group, #create-labware');
        if (event.count > 0) {
            $buttons.removeAttr('disabled');
        } else {
            $buttons.attr('disabled', 'disabled');
        }
    });

    typesQuery = $.get('/labware/get/types');
    sizesQuery = $.get('/labware/get/sizes');

    sizesQuery.then(function (data) {
        var sizes = new JsonHal(data, "sizes");

        if (sizes.present()) {
            var results = sizes.get(),
                $labwareSizes = $('#labwareSize');

            $.each(results, function (index, sizeObj) {
                var size = sizeObj.data,
                    value = size.name,
                    display = size.name + ' (' + size.columns + ' by ' + size.rows + ')';

                $labwareSizes.append('<option value="' + value + '">' + display + '</option>');
            });
        }
    });

    typesQuery.then(function (data) {
        var types = new JsonHal(data, "types");

        if (types.present()) {
            var results = types.get(),
                $labwareTypes = $('#labwareType');

            $.each(results, function (index, typeObj) {
                var type = typeObj.data,
                    value = type.name,
                    display = type.name;

                $labwareTypes.append('<option value="' + value + '">' + display + '</option>');
            });
        }
    });
});