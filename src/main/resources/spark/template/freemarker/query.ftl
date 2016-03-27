<#assign content>

<h1> Bacon: </h1>
<h3> ${db} </h3>

<form method="POST" action="/baconresults">
<label for="start">
    <textarea id="input" name="input" onkeyup="autocorrect()" placeholder="enter starting actor..." rows="1" cols="25"/></textarea>
    <div id="results"></div>
</label>
<label for="target">
    <textarea id="input1" name="input1" onkeyup="autocorrect1()"  placeholder="enter target actor..." rows="1" cols="25"/></textarea>
    <div id="results1"></div>
</label>

<br><br><br><br><br><br><br><br>

<div class='buttonWrapper'> 
<input type='submit' id='submit' value='Search'> 
 </div>
 </form>

<br>

 <div class='buttonWrapper'>    
 <form method="GET" action="/update">
<input type='submit' id='update' value='Update'> 
</form>
 </div>

</#assign>
<#include "main.ftl">