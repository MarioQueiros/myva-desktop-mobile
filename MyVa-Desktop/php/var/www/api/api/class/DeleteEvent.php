<?php

include_once 'Credentials_DB.php';

function deleteEvent($app)
{
    $request = (array) json_decode($app->request()->getBody());

    $username = $request['username'];
    $idEvents = $request['eventids'];

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
        $stmt = $pdo->prepare('SELECT PubKey FROM PubKey WHERE ID=:pub_key');
        $stmt->bindParam(':pub_key', $row['PubKey_ID']);
        $stmt->execute();
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($pubKey == $row['PubKey'])
        {
            $array = array();
            $j = 0;
            $count = count($idEvents);
            for ($i = 0; $i < $count; $i++)
            {
                foreach ($idEvents[$i] as $key => $value)
                {
                    $stmt = $pdo->prepare('UPDATE Event SET Open=0 WHERE ID=:id');
                    $stmt->bindParam(':id', $value);
                    $stmt->execute();
                    if ($stmt->rowCount() == 1)
                    {
                        $array[$j] = array('id' => $value, 'Deleted');
                    } else
                    {
                        $array[$j] = array('id' => $value, 'Event doesn\'t exist');
                    }
                }
                $j++;
            }
            echo json_encode($array);
        } else
        {
            $app->response()->status(401);
            $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'Trying to delete events from another user')));
        }
    } else
    {
        $app->response()->status(401);
        $app->response()->header('X-Status-Reason', json_encode(array('Error' => 'Username doesn\'t exists')));
    }
}
