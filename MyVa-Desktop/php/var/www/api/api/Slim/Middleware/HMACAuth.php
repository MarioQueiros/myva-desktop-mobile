<?php

/**
 * HMAC Authentication
 *
 * Use this middleware with your Slim Framework application
 * in combination with your 
 *
 * @author 9bit Studios <http://www.9bitStudios.com>
 * @version 1.0
 *
 * MIT LICENSE
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
class HMACAuth extends \Slim\Middleware
{

    /**
     * @var array
     * 
     * Route string set to tell middleware to ignore authentication
     */
    protected $allowedRoutes;

    /**
     * Constructor
     *
     * @param string  $realm The HTTP Authentication realm
     */
    public function __construct()
    {
        $this->allowedRoutes = array(
            'POST/logout',
            'POST/register',
            'POST/login',
            'GET/resendconfirmationemail'
        );
    }

    /**
     * Deny Access
     *
     */
    public function deny_access($e)
    {
        $this->app->response()->status(401);
        $this->app->response()->header('X-Status-Reason', json_encode(array('Error' => $e)));
    }

    /**
     * Check Allowed Routes
     *
     */
    public function check_allowed_routes($routeCheck)
    {

        foreach ($this->allowedRoutes as $routeString)
        {
            if ($routeCheck == $routeString)
                return true;
        }

        //if we've gotten this far, route not found
        return false;
    }

    /**
     * Check Timestamp
     *  
     * Uses the header value timestamp to check against the current timestamp
     * If the request was made within a reasonable amount of time (10 sec), 
     */
    public function check_timestamp()
    {
        $req = $this->app->request();
        $clientMicrotime = $req->headers('X-MICROTIME');
        $serverMicrotime = microtime(true);
        $timeDiff = $serverMicrotime - $clientMicrotime;
        if ($timeDiff <= 100.0)
            return true;
        else
            return false;
    }

    /**
     * Authenticate	 
     *
     * This is the authenticate method where we check the hash from the client against 
     * a hash that we will recreate here on the sevrer. If the 2 match, it's a pass.
     */
    public function authenticate($token)
    {
        $timestamp = $this->app->request()->headers('X-MICROTIME');
        $pubkey = $this->app->request()->headers('X-PUBKEY');

        $message = $this->app->request()->headers('QUERY_STRING');

        if ($pubkey != null)
        {
            $credentials = new Credentials_DB();
            $pdo = new PDO($credentials->getDSN(), $credentials->getUSER(), $credentials->getPASSWORD());
            $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

            $stmt = $pdo->prepare('SELECT ID FROM PubKey WHERE PubKey=:pubkey');
            $stmt->bindParam(':pubkey', $pubkey);
            $stmt->execute();

            if ($stmt->rowCount() > 0)
            {
                $row = $stmt->fetch(PDO::FETCH_ASSOC);

                $stmt = $pdo->prepare('SELECT PrivKey_ID FROM Users WHERE PubKey_ID=:pubkey');
                $stmt->bindParam(':pubkey', $row['ID']);
                $stmt->execute();
                $row = $stmt->fetch(PDO::FETCH_ASSOC);

                $stmt = $pdo->prepare('SELECT PrivKey FROM PrivKey WHERE ID=:privkey');
                $stmt->bindParam(':privkey', $row['PrivKey_ID']);
                $stmt->execute();
                $row = $stmt->fetch(PDO::FETCH_ASSOC);
                $hash = hash_hmac('sha1', $pubkey . $timestamp, $row['PrivKey']);

                if ($token === $hash)
                {
                    return true;
                } else
                {
                    return false;
                }
            } else
            {
                return false;
            }
        } else
        {
            return false;
        }
    }

    public function authenticateTest($token)
    {
        $timestamp = $this->app->request()->headers('X-MICROTIME');
        $username = $this->app->request()->headers('X-USERNAME');

        if ($username != null)
        {
            $credentials = new Credentials_DB();
            $pdo = new PDO($credentials->getDSN(), $credentials->getUSER(), $credentials->getPASSWORD());
            $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

            $stmt = $pdo->prepare('SELECT PubKey_ID FROM Users WHERE Username=:user');
            $stmt->bindParam(':user', $username);
            $stmt->execute();

            if ($stmt->rowCount() > 0)
            {
                $row = $stmt->fetch(PDO::FETCH_ASSOC);

                $stmt = $pdo->prepare('SELECT PrivKey_ID FROM Users WHERE PubKey_ID=:pubkey');
                $stmt->bindParam(':pubkey', $row['PubKey_ID']);
                $stmt->execute();
                $row = $stmt->fetch(PDO::FETCH_ASSOC);

                $stmt = $pdo->prepare('SELECT PrivKey FROM PrivKey WHERE ID=:privkey');
                $stmt->bindParam(':privkey', $row['PrivKey_ID']);
                $stmt->execute();
                $row = $stmt->fetch(PDO::FETCH_ASSOC);

                $hash = hash_hmac('sha1', $username . $timestamp, $row['PrivKey']);

                if ($token == $hash)
                {
                    return true;
                } else
                {
                    return false;
                }
            } else
            {
                return false;
            }
        } else
        {
            return false;
        }
    }

    /**
     * Call
     *
     * This method will check the HTTP request headers for previous authentication. If
     * the request has already authenticated, the next middleware is called. Otherwise,
     * a 401 Authentication Required response is returned to the client.
     */
    public function call()
    {
        $req = $this->app->request();

        if (!$this->check_timestamp())
        {
            $this->deny_access("The request excedded the time.");
        } else
        {
            if ($this->check_allowed_routes($req->headers('REQUEST_METHOD') . $req->getResourceUri()))
                $this->next->call();
            else
            {
                // get our hash
                $hash = $req->headers('X-HASH');
                if ($req->getResourceUri() == '/test')
                {
                    if ($this->authenticateTest($hash))
                    {
                        $this->next->call();
                    } else
                    {
                        $this->deny_access("Connection Integrity Problem");
                    }
                } else
                {

                    if ($this->authenticate($hash))
                    {
                        $this->next->call();
                    } else
                    {
                        $this->deny_access("Connection Integrity Problem");
                    }
                }
            }
        }
    }

}
