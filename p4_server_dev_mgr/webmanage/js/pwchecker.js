function checkPassword(obj) { 
pw = obj.value 
if(checkPasswordLength(pw)) 
if(checkPasswordCharsAllowed(pw)) 
if(checkMinPasswordChars(pw)) 
return true; 
return false;
} 

// Check to make sure the password is at least minChars characters long. 
function checkPasswordLength(pw) { 
if(pw.length<minChars) { 
//alert("Your password is less than "+minChars+" characters!") 
//alert("You must choose a password that is at least "+minChars+" characters in length.") 
return true 
} 
return false 
} 

// Check to make sure that all of the characters in the password are allowed. 
function checkPasswordCharsAllowed(pw) { 
for(var i=0;i<pw.length;++i) { 
var ch = pw.charAt(i); 
if((isAlpha(ch) && !lettersAllowed)) { 
//alert("Your password contains a letter!") 
//alert("Letters are not allowed in passwords.") 
return false 
}else if(isNumber(ch) && !numbersAllowed) { 
//alert("Your password contains a number!") 
//alert("Numbers are not allowed in passwords.") 
return false 
}else if(isSpecial(ch) && !specialAllowed) { 
//alert("Your password contains a special character!") 
//alert("Special characters are not allowed in passwords.") 
return false 
}else if(!isAlpha(ch) && !isNumber(ch) && !isSpecial(ch)) { 
//alert("Your password contains a non-printable character!") 
//alert("Non-printable characters are not allowed in passwords.") 
return false 
} 
} 
return true 
} 

// Check to make sure the password has the required number of alphabetic, numeric, and 
// special characters. 
function checkMinPasswordChars(pw) { 
var alpha = 0 
var numeric = 0 
var special = 0 
for(var i=0;i<pw.length;++i) { 
var ch = pw.charAt(i) 
if(isAlpha(ch)) ++alpha 
else if(isNumber(ch)) ++numeric 
else if(isSpecial(ch)) ++special 
} 
var errMsg = "Your password does not contain the minimum number " 
if(alpha < minLetters) { 
errMsg += "(" + minLetters + ") " 
errMsg += "of alphabetic characters!" 
//alert(errMsg) 
return false 
}else if(numeric < minNumbers) { 
errMsg += "(" + minNumbers + ") " 
errMsg += "of numeric characters!" 
//alert(errMsg) 
return false 
}else if(special < minSpecial) { 
errMsg += "(" + minSpecial + ") " 
errMsg += "of special characters!" 
//alert(errMsg) 
return false 
} 
return true 
} 

// Functions used for character identification. 
function isAlpha(ch) { 
if(ch >= "a" && ch <= "z") return true 
if(ch >= "A" && ch <= "Z") return true 
return false 
} 

function isNumber(ch) { 
if(ch >= "0" && ch <= "9") return true 
return false 
} 

function isSpecial(ch) { 
var special = new Array("!","\"","#","$","%","&","'","(",")","*","+",",","-",".","/", 
":",";","<","=",">","?","@","[","\\","]","^","_","`","{","|","}","~") 
for(var i=0;i<special.length;++i) 
if(ch == special[i]) return true 
return false 
} 

