import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/dev/auth/register')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/dev/auth/register"!</div>
}
