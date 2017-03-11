var animate = null;
var original = null;
var address = null;
var mail = null;
var amountToMoveTotal = null;
var outSideOfScreen = null;

function init() {
    address = document.getElementById('whiteBackground');
    address.style.opacity = 0;
    mail = document.getElementById('mailForm');
    mail.style.opacity = 0;
    original = document.getElementById('img1');
    original.style.left = '23%';
    amountToMoveTotal = parseInt(window.getComputedStyle(original).getPropertyValue('left'));
    original.style.left = '100%';
    original = null;
    address = null;
    mail = null;
}

function moveLeftContact() {
    original = document.getElementById('img1');
    outSideOfScreen = parseInt(window.getComputedStyle(original).getPropertyValue('left'));
    //Check if the image has moved the distance requested
    //If the image has not moved requested distance continue moving.
    if (outSideOfScreen > amountToMoveTotal) {
        original.style.left = (outSideOfScreen - 15) + 'px';
    	outSideOfScreen = outSideOfScreen - 15;
		
       animate = setTimeout(function() {
            moveLeftContact();
            }, 15);
    }
    else {
        original.style.left = original;
        original = null;        
        clearTimeout(animate);
        fadeIn();
    }
}

function fadeIn() {
    address = document.getElementById('whiteBackground');
    mail = document.getElementById('mailForm');
    //Check if the image has faded the amount requested
    //If the image has not faded requested amount continue fading.
    if (parseFloat(address.style.opacity) < 1.0) {
        address.style.opacity = (parseFloat(address.style.opacity) + 0.01);
    	mail.style.opacity = (parseFloat(mail.style.opacity) + 0.01);
	
        animate = setTimeout(function() {
            fadeIn();
            }, 15);
    }
    else {
        address.style.opacity = address;
        address = null;
	mail.style.opacity = mail;
	mail = null;
        clearTimeout(animate);
    }
}

function home() {
    address = document.getElementById('whiteBackground');
    original = document.getElementById('img1');
    mail = document.getElementById('mailForm');
    original.style.left = '100%';
    address.style.opacity = 0;
    mail.style.opacity = 0;
    clearTimeout(animate);
    original = null;
    address = null;
    mail = null;
}

window.onload = init;