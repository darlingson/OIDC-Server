import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/dashboard/')({ component: Dashboard })

function Dashboard() {
  return (
    <div className="p-8">
      <h1 className="text-4xl font-bold">Welcome to the Dashboard</h1>
      <p className="mt-4 text-lg">
        Edit <code>src/routes/index.tsx</code> to get started.
      </p>
    </div>
  )
}
