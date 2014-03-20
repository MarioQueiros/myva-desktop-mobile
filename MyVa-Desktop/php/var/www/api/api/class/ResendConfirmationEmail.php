<?php

include_once 'Credentials_DB.php';

function resendConfirmationEmail($app)
{
    $username = $app->request()->headers('X-USERNAME');

    try
    {
        $credentials = new Credentials_DB();
        $pdo = new PDO($credentials->getDSN(), $credentials->getUSER(), $credentials->getPASSWORD());
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    } catch (PDOException $ex)
    {
        die(json_encode(array('message' => 'Unable to connect')));
    }

    $stmt = $pdo->prepare('SELECT Email,PrivKey_ID FROM Users WHERE Username=:user');
    $stmt->bindParam(':user', $username);
    $stmt->execute();
    $row = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($stmt->rowCount() != 0)
    {
        $email = $row['Email'];
        $stmt = $pdo->prepare('SELECT PrivKey FROM PrivKey WHERE ID=:id');
        $stmt->bindParam(':id', $row['PrivKey_ID']);
        $stmt->execute();
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        sendEmail($email, "MyVa - Confirmation Code", $row['PrivKey'], $username);

        echo json_encode(array('ResendEmail' => 'Success'));
    } else
    {
        $app->response()->status(401);
        $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'Email not found')));
    }
}
