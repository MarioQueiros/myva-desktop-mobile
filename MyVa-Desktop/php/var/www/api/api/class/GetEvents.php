<?php

include_once 'Credentials_DB.php';

function getEvents($app)
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
            $json = $request['ids'];

            foreach ($request['ids'] as $idEvent)
            {
                $stmt = $pdo->prepare('SELECT ID,Local_ID,Name,TimestampEvent,DateEvent FROM Event WHERE User_ID=:user and ID=:id');
                $stmt->bindParam(':user', $userID);
                $stmt->bindParam(':id', $idEvent['id']);
                $stmt->execute();

                if ($stmt->rowCount() > 0)
                {
                    $a = array();
                    $i = 0;
                    foreach ($stmt->fetch(PDO::FETCH_ASSOC) as $row)
                    {
                        $id = $row['ID'];
                        $local = $row['Local_ID'];
                        $name = $row['Name'];
                        $timestamp = $row['TimestampEvent'];
                        $date = $row['DateEvent'];

                        $a[$i] = array('id' => $id, 'local' => $local, 'name' => $name, 'timestamp' => $timestamp, 'dateevent' => $date, 'userid' => $userID);
                        $i++;
                    }
                    $array = array('events' => $a);
		    $app->response()->header('Content-Type', 'application/json');
                    $app->response()->header('X-Status-Reason', json_encode($array));
                    echo json_encode($array);
                } else
                {
                    $app->response()->status(401);
                    $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'The user doesn\'t have any event saved')));
                }
            }
        } else
        {
            $app->response()->status(401);
            $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'Trying to get events from other user')));
        }
    } else
    {
        $app->response()->status(401);
        $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'Username doesn\'t exists')));
    }
}
