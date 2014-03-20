<?php

/**
 * Step 1: Require the Slim Framework
 *
 * If you are not using Composer, you need to require the
 * Slim Framework and register its PSR-0 autoloader.
 *
 * If you are using Composer, you can skip this step.
 */
require 'Slim/Slim.php';
require 'Slim/Middleware.php';
require 'Slim/Middleware/HMACAuth.php';
require 'class/Login.php';
require 'class/Register.php';
require 'class/Test.php';
require 'class/AddEvent.php';
require 'class/DeleteEvent.php';
require 'class/EditEvent.php';
require 'class/AddPrivateLocal.php';
require 'class/GetPublicLocals.php';
require 'class/GetPrivateLocals.php';
require 'class/GetAllEvents.php';
require 'class/GetEvents.php';
require 'class/ResendConfirmationEmail.php';

\Slim\Slim::registerAutoloader();

/**
 * Step 2: Instantiate a Slim application
 *
 * This example instantiates a Slim application using
 * its default settings. However, you will usually configure
 * your Slim application now by passing an associative array
 * of setting names and values into the application constructor.
 */
$app = new \Slim\Slim();
$app->add(new \HMACAuth());

/**
 * Step 3: Define the Slim application routes
 *
 * Here we define several Slim application routes that respond
 * to appropriate HTTP request methods. In this example, the second
 * argument for `Slim::get`, `Slim::post`, `Slim::put`, and `Slim::delete`
 * is an anonymous function.
 */
// LOGIN route
$app->post('/login', function () use ($app)
{
    //call login($app) in Login.php 
    login($app);
});

// TEST route
$app->get('/test', function () use ($app)
{
    //call test($app) in Test.php
    test($app);
});

// REGISTER route
$app->post('/register', function()use($app)
{
    //call register($app) in Register.php
    register($app);
});

// AddEvent route
$app->post('/addevent', function()use($app)
{
    //call addEvent($app) in AddEvent.php
    addEvent($app);
});

// DeleteEvent route
$app->post('/deleteevent', function()use($app)
{
    //call deleteEvent($app) in DeleteEvent.php
    deleteEvent($app);
});

// EditEvent route
$app->post('/editevent', function()use($app)
{
    //call editEvent($app) in EditEvent.php
    editEvent($app);
});

// AddPrivateLocal route
$app->post('/addprivatelocal', function()use($app)
{
    //call addPrivateLocal($app) in AddPrivateLocal.php
    addPrivateLocal($app);
});

// GetPublicLocals route
$app->get('/getpubliclocals', function()use($app)
{
    //call getPublicLocals($app) in GetPublicLocals.php
    getPublicLocals($app);
});

// GetPrivateLocals route
$app->get('/getprivatelocals', function()use($app)
{
    //call getPrivateLocals($app) in GetPrivateLocals.php
    getPrivateLocals($app);
});

// GetAllEvents route
$app->get('/getallevents', function()use($app)
{
    //call getAllEvents($app) in GetAllEvents.php
    getAllEvents($app);
});

// GetEvents route
$app->get('/getevents', function()use($app)
{
    //call getEvents($app) in GetEvents.php
    getEvents($app);
});

// ResendConfirmationEmail route
$app->get('/resendconfirmationemail', function()use($app)
{
    //call resendConfirmationEmail($app) in ResendConfirmationEmail.php
    resendConfirmationEmail($app);
});

/**
 * Step 4: Run the Slim application
 *
 * This method should be called last. This executes the Slim application
 * and returns the HTTP response to the HTTP client.
 */
$app->run();
