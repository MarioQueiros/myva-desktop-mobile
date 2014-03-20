<?php

include_once 'Credentials_DB.php';

function getPrivateLocals($app)
{
    $username = $app->request()->headers('X-USERNAME');

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
            $stmt = $pdo->prepare('SELECT Local_ID FROM UserLocal WHERE User_ID=:id');
            $stmt->bindParam(':id', $userID);
            $stmt->execute();

            if ($stmt->rowCount() > 0)
            {
                $a = array();
                $i = 0;
                $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);
                foreach ($rows as $row)
                {
                    $stmt = $pdo->prepare('SELECT ID,Latitude,Longitude,Name FROM Local WHERE ID=:id');
                    $stmt->bindParam(':id', $row['Local_ID']);
                    $stmt->execute();
                    $row1 = $stmt->fetch(PDO::FETCH_ASSOC);

                    $id = $row1['ID'];
                    $lat = utf8_encode($row1['Latitude']);
                    $long = utf8_encode($row1['Longitude']);
                    $name = utf8_encode($row1['Name']);

                    $a[$i] = array('id' => $id, 'latitude' => $lat, 'longitude' => $long, 'name' => $name);
                    $i++;
                }

                $array = array('privateLocals' => $a);

		$app->response()->header('Content-Type', 'application/json');
                $app->response()->header('X-Status-Reason', json_encode($array));

                echo json_encode($array);
            } else
            {
                $app->response()->status(401);
                $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'The user doesn\'t have any private local saved')));
            }
        } else
        {
            $app->response()->status(401);
            $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'Trying to get events from another user')));
        }
    } else
    {
        $app->response()->status(401);
        $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'Username doesn\'t exists')));
    }
}
