function showChangePassField(rownum) {
    let index = parseInt(rownum);
    let passwd = document.createElement("input");
    passwd.type = "password";
    passwd.id = "users"+rownum+".password";
    passwd.name = "users["+rownum+"].password";
    let row = document.getElementsByTagName("tbody")[0]
                      .getElementsByTagName("tr")[index]
                      .getElementsByTagName("td");
    row[row.length - 1].appendChild(passwd);
}