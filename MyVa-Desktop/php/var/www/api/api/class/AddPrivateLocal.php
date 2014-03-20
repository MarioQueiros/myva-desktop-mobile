<?php

include_once 'Credentials_DB.php';

function addPrivateLocal($app)
{
    $request = (array) json_decode($app->request()->getBody());
    $username = $request['username'];

    $pubKey = $app->request()->headers('X-PUBKEY');

    try
    {
        $credentials = new Credentials_DB();
        $pdo = new PDO($credentials->getDSN(), $credentials->getUSER(), $credentials->getPASSWORD());
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    } catch (PDOException $ex)
    {
        die(json_encode(array('message' => 'Unable to connect')));
    }

    $stmt = $pdo->prepare('SELECT PubKey_ID,ID FROM Users WHERE Username=:user');
    $stmt->bindParam(':user', $username);
    $stmt->execute();
    $row = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($stmt->rowCount() != 0)
    {
        $userID = $row['ID'];
        
        $stmt = $pdo->prepare('SELECT PubKey FROM PubKey WHERE ID=:pub_key');
        $stmt->bindParam(':pub_key', $row['PubKey_ID']);
        $stmt->execute();
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($pubKey == $row['PubKey'])
        {
            $name = $request['name'];
            $latitude = $request['latitude'];
            $longitude = $request['longitude'];

            $stmt = $pdo->query('SELECT UUID()');
            $res = $stmt->fetchAll(PDO::PARAM_STR);
            foreach ($res[0] as $key => $useless)
            {
                $localuuid = $useless;
            }

            $stmt = $pdo->prepare('INSERT INTO Local (ID,Latitude,Longitude,Name) values (:id,:lat,:long,:name)');
            $stmt->bindParam(':id', $localuuid);
            $stmt->bindParam(':lat', $latitude);
            $stmt->bindParam(':long', $longitude);
            $stmt->bindParam(':name', $name);
            $stmt->execute();
            
            //*********//
            
            $stmt = $pdo->prepare('INSERT INTO UserLocal (User_ID,LocalID) values (:user,:local)');
            $stmt->bindParam(':user', $localuuid);
            $stmt->bindParam(':local', $userID);
            $stmt->execute();

            echo json_decode(array('AddPrivateLocalID' => $localuuid));
        } else
        {
            $app->response()->status(401);
            $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'Trying to add private locals to other user')));
        }
    } else
    {
        $app->response()->status(401);
        $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'Username doesn\'t exists')));
    }
}
