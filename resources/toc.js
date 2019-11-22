var timer;
window.onscroll = function() {
	if (timer) {
		clearTimeout(timer)
	}
	var tocDiv = document.getElementById("tocDiv")
	tocDiv.style.display = "block"
    timer = setTimeout(function () {
        tocDiv.style.display = "none"
    }, 3000);
}