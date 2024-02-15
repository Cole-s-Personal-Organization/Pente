

import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import { Input } from "@/components/ui/input"

import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { z } from "zod"

import React, { useEffect, useState } from 'react'
import PenteGameCreationForm from './components/PenteGameCreationForm';


interface GameHeader {
  gameId: string,
  lobbyName: string,
  gameHost: string,
  timeCreatedAt: string,
  gameRunState: "Created" | "Running" | "Ended"
}

const formSchema = z.object({
  username: z.string().min(2, {
      message: "Username must be at least 2 characters.",
  }),
})
const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      username: "",
    },
})

const GameLobbies: React.FC = () => {

  const [gameHeaders, setGameHeaders] = useState<GameHeader[] | null>(null);
  const [creatingGame, setCreatingGame] = useState<boolean>(false);

  const getLobbiesFromGameServer = async () => {
    try {
      const response = await fetch("http://localhost:8080/gameserver/pente-game/list/head");

      if (!response.ok) {
        throw new Error("Failed to fetch lobby data.");
      }

      await response.json().then(data => {
        setGameHeaders(data.gameHeaders);
        console.log(gameHeaders);
      });
      
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  }

  const onAddGameClick = () => {
    console.log("Adding Game");
    setCreatingGame(true);
  }

  function onSubmit(values: z.infer<typeof formSchema>) {
    console.log(values)
  }


  useEffect(() => {
    getLobbiesFromGameServer()
  }, []);

  return (
    <div>
      <h1>Game Lobbies</h1>
      {gameHeaders === null ? (
        <p>Loading...</p>
      ) : gameHeaders.length === 0 ? (
        <p>There are no games.</p>
      ) : (
        <div>
          {gameHeaders.map((gameHeader, index) => (
            <div key={index}>
              {/* Render each game header's details */}
            </div>
          ))}
        </div>
      )}

      <Dialog>
        <DialogTrigger><Button onClick={onAddGameClick}>Add Game</Button></DialogTrigger>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Create Game</DialogTitle>
            <DialogDescription>
              Create a new game to be listed among other availible games
            </DialogDescription>
          </DialogHeader>
          {/* <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
            <FormField
                control={form.control}
                name="username"
                render={({ field }) => (
                    <FormItem>
                    <FormLabel>Username</FormLabel>
                    <FormControl>
                        <Input placeholder="shadcn" {...field} />
                    </FormControl>
                    <FormDescription>
                        This is your public display name.
                    </FormDescription>
                    <FormMessage />
                    </FormItem>
                )}
                />
                <Button type="submit">Submit</Button>
            </form>
          </Form> */}
          <DialogFooter>

          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}

export default GameLobbies