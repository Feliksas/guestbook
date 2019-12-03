function submitGuestBookEntry(div, parent) {
    let id;
    let parent_id = null;
    let message = div.getElementsByClassName("message")[0].value;

    try {
        id = div.getElementsByClassName("message-id")[0].value;
    } catch (err) {
        id = null;
    }

    if(parseInt(parent, 10) !== -1) {
        parent_id = parent;
    }
    $.ajax({
        type: "POST",
        url: "/edit",
        data: {
            "id": id,
            "parentMsgId": parent_id,
            "content": message
        },
        complete: function (jqxhr, status) {
            location.reload();
        }
    });
}

function deleteEntry(div) {
    let postId = div.getElementsByTagName("input")[0].value;
    $.ajax({
        type: "DELETE",
        url: "/delete",
        data: "id="+postId,
        complete: function (jqxhr, status) {
            location.reload();
        }
    });
}

function focusMessage(div) {
    let message =  div.getElementsByClassName("message")[0];
    message.removeAttribute("readonly");
    message.focus();
}

function displaySave(div) {
    div.getElementsByClassName("btn save")[0].hidden=false;
}

function addReply(div) {
    let replyBlock = document.createElement("div");
    let replyField = document.createElement("textarea");
    replyField.classList.add("message");

    let sendReplyBtn = document.createElement("button");
    sendReplyBtn.type = "button";
    sendReplyBtn.classList.add("btn","btn-primary");

    let parentId = div.getElementsByClassName("message-id")[0].value;
    sendReplyBtn.setAttribute("onclick", "submitGuestBookEntry(this.parentElement,"+parentId+")");
    sendReplyBtn.textContent = "Send";

    replyBlock.appendChild(replyField);
    replyBlock.appendChild(sendReplyBtn);
    div.appendChild(replyBlock);
}