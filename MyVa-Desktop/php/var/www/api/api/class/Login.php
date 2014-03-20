<?php

function login($app)
{
    $request = (array) json_decode($app->request()->getBody());
    $username = $request['username'];
    $password = $request['password'];
    
    if($username === 'demo' && $password === 'demo') {
	
	$app->setCookie('PUBLIC-KEY', 'demo', '1 hour');
	$userArray = array(
	    'username' => $username
	); 	
	echo json_encode($userArray);
	 
    }
    else {
	$app->response()->status(401);
    }
}

function register($app)
{
    $request = (array) json_decode($app->request()->getBody());
    echo $app->request()->getBody();
    $username = $request['username'];
    $password = $request['password'];
    
    if($username === 'demo' && $password === 'demo') {
	
	$app->setCookie('PUBLIC-KEY', 'demo', '1 hour');
	$userArray = array(
	    'username' => $username
	); 	
	echo json_encode($userArray);
	 
    }
    else {
	$app->response()->status(401);
    }
}