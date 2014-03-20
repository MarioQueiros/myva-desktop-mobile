<?php

include_once 'Credentials_DB.php';
include_once 'Utils.php';
include_once 'passwordLib.php';

function test($app)
{
    $username = $app->request()->headers('X-USERNAME');

    $conf = "";
    $conf = $app->request()->headers('X-RESENDTYPE');
    if ($conf != "")
    {
        try
        {
            $credentials = new Credentials_DB();
            $pdo = new PDO($credentials->getDSN(), $credentials->getUSER(), $credentials->getPASSWORD());
            $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        } catch (PDOException $ex)
        {
            die(json_encode(array('message' => 'Unable to connect')));
        }

        $stmt = $pdo->prepare('SELECT ID,Email,Username,Birthday,RegConf FROM Users WHERE Username=:user');
        $stmt->bindParam(':user', $username);
        $stmt->execute();
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($stmt->rowCount() != 0)
        {
            $id = $row['ID'];
            $email = $row['Email'];
            $username = $row['Username'];
            $birth = $row['Birthday'];
            echo json_encode(array('Success' => 'Account confirmed', 'id' => $id, 'username' => $username, 'email' => $email, 'birthday' => $birth));
        } else
        {
            $app->response()->status(401);
            $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'User not registered.')));
        }
    } else
    {
        try
        {
            $credentials = new Credentials_DB();
            $pdo = new PDO($credentials->getDSN(), $credentials->getUSER(), $credentials->getPASSWORD());
            $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        } catch (PDOException $ex)
        {
            die(json_encode(array('message' => 'Unable to connect')));
        }

        $stmt = $pdo->prepare('SELECT ID,Email,Username,Birthday,RegConf FROM Users WHERE Username=:user');
        $stmt->bindParam(':user', $username);
        $stmt->execute();
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($stmt->rowCount() != 0)
        {
            if ($row['RegConf'] == 0)
            {
                $reg = 1;
                $stmt = $pdo->prepare('UPDATE Users SET RegConf=:reg WHERE Username=:user');
                $stmt->bindParam(':reg', $reg, PDO::PARAM_INT);
                $stmt->bindParam(':user', $username);
                $stmt->execute();

                echo json_encode(array('Success' => 'Username confirmed.'));
            } else
            {
                $app->response()->status(401);
                $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'User already confirmed.')));
            }
        } else
        {
            $app->response()->status(401);
            $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'User not registered.')));
        }
    }
}
