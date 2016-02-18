/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*Add smooth transition between pics later*/

var pic = 2;

function movePic(x) {
    var image = document.getElementById('PhotoBox');
    var title = document.getElementById("title");
    var desc = document.getElementById('description');
    
    pic = (((pic + x) % 3) + 3) % 3;  /*Correct way to do Modulo*/
    
    if(pic === 0 ){
        title.innerHTML = "Evil Genius";
        image.style.backgroundImage = 'url(EmilyPic.jpg)';
        desc.innerHTML = "Emily MacDonald is a senior at Whitworth University, studying Physics and French, with minors in Spanish, Sociology, and Math. She works as a night manager at a restaurant. She does not enjoy long walks on the beach, but is not opposed to vanilla milkshakes or Pina Coladas. She is quite fond of burgers and randomizing articles on Wikipedia. The majority of her knowledge comes from Nancy Drew or Sherlock Holmes computer games from her youth. She likes science. Her favourite colour is usually green. She often uses the British spelling of words and is a strong supporter of the Oxford comma.";
    }
    else if (pic === 1){
        title.innerHTML = "Harpoon Engagement Planning Officer";
        image.style.backgroundImage = 'url(ChrisPic.jpg)';
        desc.innerHTML = "Christopher Peryeda is a student at Whitworth University studying Computer Science, Mathematics, and Applied Physics. He is currently looking into grad school to attend after he graduates next year. An aspiring math dude who loves cool math-y things that are awesome. Enjoys PB&J for lunch and is a proud chocoholic. When not singing in the shower, can often be found looking smart and or sleeping.";
    }
    else{
        title.innerHTML = "BrewMaster";
        image.style.backgroundImage = 'url(Pete.jpg)';
        desc.innerHTML = "This is Pete!";
    }   
}

/***********THIS FUNCTION BY w3schools.com******************/

/* When the user clicks on the button, 
toggle between hiding and showing the dropdown content */
function myFunction() {
    document.getElementById("myDropdown").classList.toggle("show");
}

// Close the dropdown menu if the user clicks outside of it
window.onclick = function(event) {
  if (!event.target.matches('.dropbtn')) {

    var dropdowns = document.getElementsByClassName("dropdown-content");
    var i;
    for (i = 0; i < dropdowns.length; i++) {
      var openDropdown = dropdowns[i];
      if (openDropdown.classList.contains('show')) {
        openDropdown.classList.remove('show');
      }
    }
  }
}