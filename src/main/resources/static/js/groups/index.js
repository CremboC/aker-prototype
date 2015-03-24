/**
 * Created by pi1 on 17/03/2015.
 */
$(document).ready(function () {

    var $groups = $('#jqp-groups');

    $groups.pager({
        url: '/groups/json/',
        template: '#group-template',
        loadButton: '#jqp-load-more-groups',
        appendBefore: '#jqp-append-before-groups'
    });

    $groups.selectableElement({
        element: 'tbody .selectable'
    });

    $('#create-group').group({
        modal: '#groupModal',
        confirm: '#modalConfirm',
        mode: 'groups',
        template: '#modal-group-template'
    });

    $groups.on('element.selected', function (event) {
        var $button = $('#create-group');
        if (event.count > 0) {
            $button.removeAttr('disabled');
        } else {
            $button.attr('disabled', 'disabled');
        }
    });
});