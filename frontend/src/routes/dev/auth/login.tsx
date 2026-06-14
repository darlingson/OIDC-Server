import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/dev/auth/login')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/dev/auth/login"!</div>
}
