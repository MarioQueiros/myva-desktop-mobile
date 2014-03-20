<?php

include_once 'Credentials_DB.php';

function addEvent($app)
{
    $username = $app->request()->headers('X-USERNAME');
    $name = $app->request()->headers('X-NAME');
    $date = $app->request()->headers('X-DATE');
    $localId = "";
    $localName = "";
    $hasID = false;

    $localName = $app->request()->headers('X-LOCALNAME');
    if ($localName != "")
    {
        $latitude = $app->request()->headers('X-LATITUDE');
        $longitude = $app->request()->headers('X-LONGITUDE');
    } else
    {
        $localId = $app->request()->headers('X-LOCALID');
        $hasID = true;
    }

    $newdate = "";
    $hasDate = false;
    try
    {
        $newdate = date('Y-m-d', $date/1000);
        $hasDate = true;
    } catch (Exception $ex)
    {
        $hasDate = false;
    }
    if ($hasDate)
    {
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
                $haslocal = false;
                if ($hasID == false)
                {
                    $stmt = $pdo->query('SELECT UUID()');
                    $res = $stmt->fetchAll(PDO::PARAM_STR);
                    foreach ($res[0] as $key => $useless)
                    {
                        $localId = $useless;
                    }

                    $stmt = $pdo->prepare('INSERT INTO Local (ID,Latitude,Longitude,Name) values (:id,:lat,:long,:name)');
                    $stmt->bindParam(':id', $localId);
                    $stmt->bindParam(':lat', $latitude);
                    $stmt->bindParam(':long', $longitude);
                    $stmt->bindParam(':name', $localName);
                    $stmt->execute();

                    //*********//
                    $stmt = $pdo->query('SELECT UUID()');
                    $res = $stmt->fetchAll(PDO::PARAM_STR);
                    foreach ($res[0] as $key => $useless)
                    {
                        $nid = $useless;
                    }

                    $stmt = $pdo->prepare('INSERT INTO UserLocal (ID,User_ID,Local_ID) values (:id,:user,:local)');
                    $stmt->bindParam(':id', $nid);
                    $stmt->bindParam(':user', $userID);
                    $stmt->bindParam(':local', $localId);
                    $stmt->execute();
                    $haslocal = true;
                } else
                {
                    $stmt = $pdo->prepare('SELECT ID FROM Local WHERE ID=:id');
                    $stmt->bindParam(':id', $localId);
                    $stmt->execute();
                    $row = $stmt->fetch(PDO::FETCH_ASSOC);

                    if ($stmt->rowCount() > 0)
                    {
                        $localId = $row['ID'];
                        $haslocal = true;
                    }
                }

                if ($haslocal == true)
                {
                    $stmt = $pdo->query('SELECT UUID()');
                    $res = $stmt->fetchAll(PDO::PARAM_STR);
                    foreach ($res[0] as $key => $useless)
                    {
                        $id = $useless;
                    }

                    $stmt = $pdo->prepare('INSERT INTO Event (ID,Local_ID,User_ID,Name,TimestampEvent,DateEvent,Open) values (:id,:local,:user,:name,UNIX_TIMESTAMP(),:date,1)');
                    $stmt->bindParam(':id', $id);
                    $stmt->bindParam(':local', $localId);
                    $stmt->bindParam(':user', $userID);
                    $stmt->bindParam(':name', $name);
                    $stmt->bindParam(':date', $newdate);
                    $stmt->execute();

                    $stmt = $pdo->prepare('SELECT TimestampEvent FROM Event WHERE ID=:id');
                    $stmt->bindParam(':id', $id);
                    $stmt->execute();
                    $row = $stmt->fetch(PDO::FETCH_ASSOC);

                    echo json_encode(array('AddEventID' => $id, 'LocalID' => $localId, 'Timestamp' => $row['TimestampEvent']));
                } else
                {
                    $app->response()->status(401);
                    $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'Local doesn\'t exist')));
                }
            } else
            {
                $app->response()->status(401);
                $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'Trying to add events to other user')));
            }
        } else
        {
            $app->response()->status(401);
            $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'Username doesn\'t exists')));
        }
    } else
    {
        $app->response()->status(401);
        $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'Wrong date format')));
    }
}
