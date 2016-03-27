<#assign content>

<h1> Update Bacon: </h1>
<h3> ${db} </h3>

<form class="updatepage">
<label for="update">
    <textarea id="aAdd" placeholder="enter actor to add..." rows="1" cols="25"/></textarea>
</label>
<div class='buttonWrapper'> 
<input type='submit' id='addActor' name='addActor' value='Add actor'> 
 </div>
</form>

<br>

<form class="updatepage">
<label for="update">
    <textarea id="fAdd" placeholder="enter film to add..." rows="1" cols="25"/></textarea>
</label>
<div class='buttonWrapper'> 
<input type='submit' id='addFilm' name='addFilm' value='Add film'> 
 </div>
</form>

<br><br>

<form class="updatepage">
<label for="update">
    <textarea id="aMod" placeholder="enter actor..." rows="1" cols="25"/></textarea>
    </label>
    <label for="update">
    <textarea id="fMod" placeholder="enter film..." rows="1" cols="25"/></textarea>
</label>
<br><br>
<div class='buttonWrapper'> 
<input type='submit' id='addAToF' name='addAToF' value='Add actor to film'> 
 </div><br>
<div class='buttonWrapper'> 
<input type='submit' id='removeAFromF' name='removeAFromF' value='Remove actor from film'> 
 </div> 
</form>

<br><br><br>

<div class='buttonWrapper'> 
<form method="GET" action="/home">   
<input type='submit' id='submit' value='Run a search'> 
</form>
 </div>

</#assign>
<#include "main.ftl">